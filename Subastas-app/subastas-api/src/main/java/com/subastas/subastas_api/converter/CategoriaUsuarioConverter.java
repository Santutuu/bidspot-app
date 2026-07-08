package com.subastas.subastas_api.converter;

import com.subastas.subastas_api.model.CategoriaUsuario;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CategoriaUsuarioConverter
        implements AttributeConverter<CategoriaUsuario, String> {

    @Override
    public String convertToDatabaseColumn(CategoriaUsuario categoria) {
        if (categoria == null) {
            return null;
        }

        return switch (categoria) {
            case COMUN -> "comun";
            case PLATA -> "plata";
            case ORO -> "oro";
            case PLATINO -> "platino";
        };
    }

    @Override
    public CategoriaUsuario convertToEntityAttribute(String valorDb) {
        if (valorDb == null) {
            return null;
        }

        return switch (valorDb.toLowerCase()) {
            case "comun" -> CategoriaUsuario.COMUN;
            case "plata" -> CategoriaUsuario.PLATA;
            case "oro" -> CategoriaUsuario.ORO;
            case "platino" -> CategoriaUsuario.PLATINO;
            default -> throw new IllegalArgumentException(
                    "Categoría de usuario inválida en la base de datos: " + valorDb
            );
        };
    }
}