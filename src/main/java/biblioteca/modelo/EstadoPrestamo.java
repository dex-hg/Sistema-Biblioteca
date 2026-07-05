package biblioteca.modelo;

/**
 * Estados válidos de un préstamo, alineados con el CHECK constraint de la BD:
 * CHECK (estado IN ('ACTIVO', 'DEVUELTO', 'ATRASADO'))
 */
public enum EstadoPrestamo {
    ACTIVO,
    DEVUELTO,
    ATRASADO;

    /** Convierte el String de la BD al enum correspondiente. */
    public static EstadoPrestamo fromString(String valor) {
        if (valor == null) return ACTIVO;
        return switch (valor.toUpperCase()) {
            case "DEVUELTO" -> DEVUELTO;
            case "ATRASADO" -> ATRASADO;
            default         -> ACTIVO;
        };
    }
}
