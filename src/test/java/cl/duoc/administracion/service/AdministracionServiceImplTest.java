package cl.duoc.administracion.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import cl.duoc.administracion.dto.CategoriaRequest;
import cl.duoc.administracion.dto.ComentarioRequest;
import cl.duoc.administracion.dto.PublicacionRequest;
import cl.duoc.administracion.exception.ConflictoException;
import cl.duoc.administracion.exception.RecursoNoEncontradoException;
import cl.duoc.administracion.model.Categoria;
import cl.duoc.administracion.model.Comentario;
import cl.duoc.administracion.model.Publicacion;
import cl.duoc.administracion.repository.CategoriaRepository;
import cl.duoc.administracion.repository.ComentarioRepository;
import cl.duoc.administracion.repository.PublicacionRepository;

class AdministracionServiceImplTest {

    private CategoriaRepository categoriaRepository;
    private PublicacionRepository publicacionRepository;
    private ComentarioRepository comentarioRepository;

    private CategoriaServiceImpl categoriaService;
    private PublicacionServiceImpl publicacionService;
    private ComentarioServiceImpl comentarioService;

    @BeforeEach
    void setUp() {
        categoriaRepository = mock(CategoriaRepository.class);
        publicacionRepository = mock(PublicacionRepository.class);
        comentarioRepository = mock(ComentarioRepository.class);

        categoriaService = new CategoriaServiceImpl(categoriaRepository);
        publicacionService = new PublicacionServiceImpl(publicacionRepository, categoriaRepository);
        comentarioService = new ComentarioServiceImpl(comentarioRepository, publicacionRepository);

        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(i -> i.getArgument(0));
        when(publicacionRepository.save(any(Publicacion.class))).thenAnswer(i -> i.getArgument(0));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(i -> i.getArgument(0));
    }

    // ===== Categorias =====

    @Test
    void categoriaInexistenteLanza404() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> categoriaService.obtenerCategoriaPorId(99L));
    }

    @Test
    void categoriaDuplicadaLanza409() {
        when(categoriaRepository.existsByNombre("Tecnologia")).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> categoriaService.crearCategoria(new CategoriaRequest("Tecnologia", "Duplicada")));
    }

    @Test
    void eliminarCategoriaConPublicacionesLanza409() {
        Categoria categoria = new Categoria("Tecnologia", "Con publicaciones");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        doThrow(new DataIntegrityViolationException("FK")).when(categoriaRepository).flush();

        assertThrows(ConflictoException.class, () -> categoriaService.eliminarCategoria(1L));
    }

    // ===== Publicaciones =====

    @Test
    void banearYReactivarPublicacion() {
        Publicacion publicacion = new Publicacion("Titulo", "Contenido", 2L, 1L);
        when(publicacionRepository.findById(1L)).thenReturn(Optional.of(publicacion));

        assertEquals("BANEADO", publicacionService.banearPublicacion(1L).estado());
        assertEquals("ACTIVO", publicacionService.reactivarPublicacion(1L).estado());
    }

    @Test
    void crearPublicacionEnCategoriaInexistenteLanza404() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> publicacionService.crearPublicacion(
                new PublicacionRequest("Titulo", "Contenido", 2L, 99L)));
    }

    // ===== Comentarios =====

    @Test
    void banearComentario() {
        Comentario comentario = new Comentario("Comentario ofensivo", 1L, 3L);
        when(comentarioRepository.findById(5L)).thenReturn(Optional.of(comentario));

        assertEquals("BANEADO", comentarioService.banearComentario(5L).estado());
    }

    @Test
    void comentarPublicacionInexistenteLanza404() {
        when(publicacionRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class,
                () -> comentarioService.crear(new ComentarioRequest("Hola", 99L, 2L)));
    }
}
