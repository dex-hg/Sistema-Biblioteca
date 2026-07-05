package biblioteca.modelo;

public class DetallePrestamo {

    private int id;
    private Prestamo prestamo;
    private Libro libro;
    private int cantidad;

    public DetallePrestamo() {
    }

    public DetallePrestamo(int id, Prestamo prestamo, Libro libro, int cantidad) {
        this.id = id;
        this.prestamo = prestamo;
        this.libro = libro;
        this.cantidad = cantidad;
    }

    public DetallePrestamo(Prestamo prestamo, Libro libro, int cantidad) {
        this.prestamo = prestamo;
        this.libro = libro;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
