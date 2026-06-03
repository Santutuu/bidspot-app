
package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.DetalleSubastaDTO;
import com.subastas.subastas_api.DTO.SubastaHomeDTO;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.service.DetalleSubastaService;
import com.subastas.subastas_api.service.SubastaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/subastas")
public class SubastaController {

    private final SubastaService subastaService;
    private final DetalleSubastaService detalleSubastaService;

    public SubastaController(SubastaService subastaService,
                             DetalleSubastaService detalleSubastaService) {

        this.subastaService = subastaService;
        this.detalleSubastaService = detalleSubastaService;
    }

    /*
     * GET /subastas/recomendadas

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
     * GET /subastas/{id}
     *
     * Obtiene toda la información necesaria para la pantalla
     * de detalle de una subasta.

     * Reglas:
     *
     * Errores:
     * 401 Unauthorized -> El usuario debe estar autenticado -> Pendiente utilizar JWT
     */
    @GetMapping("/{id}")
    public ResponseEntity<DetalleSubastaDTO> obtenerDetalleSubasta(@PathVariable Long id) {

        DetalleSubastaDTO response =
                detalleSubastaService.obtenerDetalleSubasta(id);

        return ResponseEntity.ok(response);
    }
}

