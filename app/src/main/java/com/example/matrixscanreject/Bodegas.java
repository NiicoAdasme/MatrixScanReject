package com.example.matrixscanreject;

public class Bodegas {
    String bodega_id;
    String bodega_nombre;

    public String getBodega_id() {
        return bodega_id;
    }

    public void setBodega_id(String bodega_id) {
        this.bodega_id = bodega_id;
    }

    public String getBodega_nombre() {
        return bodega_nombre;
    }

    public void setBodega_nombre(String bodega_nombre) {
        this.bodega_nombre = bodega_nombre;
    }

    @Override
    public String toString() {
        return this.bodega_nombre;
    }

    public Bodegas(String bodega_id, String bodega_nombre) {
        this.bodega_id = bodega_id;
        this.bodega_nombre = bodega_nombre;
    }
}
