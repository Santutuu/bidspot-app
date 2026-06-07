package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.CategoriaSubastasDTO;
import com.subastas.subastas_api.DTO.DetalleSubastaDTO;
import com.subastas.subastas_api.DTO.SubastaHomeDTO;
import com.subastas.subastas_api.model.Categoria;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.service.CategoriaService;
import com.subastas.subastas_api.service.DetalleSubastaService;
import com.subastas.subastas_api.service.SubastaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subastas")
public class SubastaController {

    private final SubastaService subastaService;
    private final DetalleSubastaService detalleSubastaService;
    private final CategoriaService categoriaService;

    public SubastaController(SubastaService subastaService,
                             DetalleSubastaService detalleSubastaService,
                             CategoriaService categoriaService) {

        this.subastaService = subastaService;
        this.detalleSubastaService = detalleSubastaService;
        this.categoriaService = categoriaService;
    }

    /*
     * GET /subastas/recomendadas
     *
     * Obtiene las subastas recomendadas para el Home.
     *
     * Reglas:
     * - Si el usuario está autenticado, puede ver precioActual.
     * - Si el usuario no está autenticado, precioActual viene en null.
     * - Si el usuario es dueño de una subasta, esa subasta no debería aparecerle.
     */
    @GetMapping("/recomendadas")
    public ResponseEntity<List<SubastaHomeDTO>> obtenerRecomendadas(
            @RequestParam(defaultValue = "4") int limit) {

        /*
         * Cuando tengas seguridad/JWT implementado,
         * este usuario debería obtenerse desde el token.
         */
        Usuario usuarioActual = null;

        List<SubastaHomeDTO> response =
                subastaService.obtenerSubastasRecomendadas(limit, usuarioActual);

        return ResponseEntity.ok(response);
    }

    /*
     * GET /subastas/categoria/{categoria}
     *
     * Obtiene las subastas de una categoría seleccionada desde el Home.
     *
     * Divide la respuesta en:
     * - subastas en tiempo real
     * - subastas programadas
     *
     * Reglas:
     * - Si el usuario está autenticado, puede ver precioActual en subastas abiertas.
     * - Si no está autenticado, precioActual viene en null en subastas abiertas.
     * - Las subastas programadas muestran precioInicial.
     * - Si el usuario es dueño de una subasta, esa subasta no debería aparecerle.
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<CategoriaSubastasDTO> obtenerSubastasPorCategoria(
            @PathVariable Categoria categoria) {

        /*
         * Cuando tengas seguridad/JWT implementado,
         * este usuario debería obtenerse desde el token.
         */
        Usuario usuarioActual = null;

        CategoriaSubastasDTO response =
                categoriaService.obtenerSubastasPorCategoria(categoria, usuarioActual);

        return ResponseEntity.ok(response);
    }


    /*
     * GET /subastas/{id}
     *
     * Obtiene toda la información necesaria para la pantalla
     * de detalle de una subasta.
     *
     * Reglas:
     * - El usuario debe estar autenticado para ingresar al detalle.
     *
     * Errores:
     * - 401 Unauthorized -> Pendiente implementar con JWT/Security.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DetalleSubastaDTO> obtenerDetalleSubasta(
            @PathVariable Long id) {

        DetalleSubastaDTO response =
                detalleSubastaService.obtenerDetalleSubasta(id);

        return ResponseEntity.ok(response);
    }


}