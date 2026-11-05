package com.utch.parcelas.service;

import com.utch.parcelas.exception.DuplicateParcelException;
import com.utch.parcelas.exception.ParcelNotFoundException;
import com.utch.parcelas.model.Parcel;
import com.utch.parcelas.repository.ParcelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParcelService: pruebas unitarias con Mockito")
class ParcelServiceTest {

    // El repositorio se simula: no se necesita base de datos ni contexto de Spring
    @Mock
    private ParcelRepository parcelRepository;

    // Mockito crea el servicio e inyecta el repositorio simulado en su constructor
    @InjectMocks
    private ParcelService parcelService;

    private Parcel huertoNorte;
    private Parcel huertoSur;

    @BeforeEach
    void setUp() {
        huertoNorte = new Parcel("Huerto Norte", "arcilloso", "Ana López");
        huertoNorte.setId(UUID.randomUUID());
        huertoSur = new Parcel("Huerto Sur", "arenoso", "Luis Pérez");
        huertoSur.setId(UUID.randomUUID());
    }

    // ------------------------------------------------------------ findAll

    @Test
    @DisplayName("findAll devuelve la lista de parcelas que entrega el repositorio")
    void findAll_conParcelas_devuelveLaLista() {
        when(parcelRepository.findAll()).thenReturn(List.of(huertoNorte, huertoSur));

        List<Parcel> resultado = parcelService.findAll();

        assertThat(resultado)
                .hasSize(2)
                .extracting(Parcel::getName)
                .containsExactly("Huerto Norte", "Huerto Sur");
        verify(parcelRepository).findAll();
    }

    @Test
    @DisplayName("findAll devuelve una lista vacía cuando no hay parcelas")
    void findAll_sinParcelas_devuelveListaVacia() {
        when(parcelRepository.findAll()).thenReturn(List.of());

        List<Parcel> resultado = parcelService.findAll();

        assertThat(resultado).isEmpty();
        verify(parcelRepository).findAll();
    }

    // ------------------------------------------------------------ findById

    @Test
    @DisplayName("findById devuelve la parcela cuando existe")
    void findById_existente_devuelveLaParcela() {
        when(parcelRepository.findById(huertoNorte.getId())).thenReturn(Optional.of(huertoNorte));

        Parcel resultado = parcelService.findById(huertoNorte.getId());

        assertThat(resultado).isSameAs(huertoNorte);
    }

    @Test
    @DisplayName("findById lanza ParcelNotFoundException cuando no existe")
    void findById_inexistente_lanzaExcepcion() {
        UUID idInexistente = UUID.randomUUID();
        when(parcelRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelService.findById(idInexistente))
                .isInstanceOf(ParcelNotFoundException.class)
                .hasMessageContaining(idInexistente.toString());
    }

    // ------------------------------------------------------------ create

    @Test
    @DisplayName("create guarda la parcela, limpia el nombre y conserva los atributos")
    void create_datosValidos_guardaLaParcela() {
        UUID idGenerado = UUID.randomUUID();
        when(parcelRepository.existsByNameIgnoreCase("Huerto Norte")).thenReturn(false);
        // Simula lo que haría la base de datos: asignar el identificador al guardar
        when(parcelRepository.save(any(Parcel.class))).thenAnswer(invocacion -> {
            Parcel guardada = invocacion.getArgument(0);
            guardada.setId(idGenerado);
            return guardada;
        });

        Parcel entrada = new Parcel("  Huerto Norte  ", "arcilloso", "Ana López");
        Parcel creada = parcelService.create(entrada);

        // Se verifica lo que recibió el repositorio
        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(parcelRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Huerto Norte");

        // Se verifica lo que devolvió el servicio
        assertThat(creada.getId()).isEqualTo(idGenerado);
        assertThat(creada.getName()).isEqualTo("Huerto Norte");
        assertThat(creada.getSoilType()).isEqualTo("arcilloso");
        assertThat(creada.getOwnerOrLead()).isEqualTo("Ana López");
    }

    @Test
    @DisplayName("create lanza DuplicateParcelException si el nombre ya existe y no guarda")
    void create_nombreDuplicado_lanzaExcepcionYNoGuarda() {
        when(parcelRepository.existsByNameIgnoreCase("Huerto Norte")).thenReturn(true);

        Parcel entrada = new Parcel("Huerto Norte", "arcilloso", "Ana López");

        assertThatThrownBy(() -> parcelService.create(entrada))
                .isInstanceOf(DuplicateParcelException.class)
                .hasMessageContaining("Huerto Norte");
        verify(parcelRepository, never()).save(any(Parcel.class));
    }

    @ParameterizedTest(name = "create rechaza el nombre [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("create rechaza nombres nulos o vacíos sin tocar el repositorio")
    void create_nombreInvalido_lanzaExcepcion(String nombre) {
        Parcel entrada = new Parcel(nombre, "arcilloso", "Ana López");

        assertThatThrownBy(() -> parcelService.create(entrada))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obligatorio");
        verifyNoInteractions(parcelRepository);
    }

    @Test
    @DisplayName("create rechaza una parcela nula")
    void create_parcelaNula_lanzaExcepcion() {
        assertThatThrownBy(() -> parcelService.create(null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(parcelRepository);
    }
}
