package com.utn.tpi.ms2.refactor.domain;

public enum Prioridad {
    ALTA(1),
    MEDIA(2),
    BAJA(3);

    private final int nivel;

    Prioridad(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

    public static Prioridad fromNivel(int nivel) {
        for (Prioridad p : values()) {
            if (p.nivel == nivel) return p;
        }
        throw new IllegalArgumentException("Prioridad invalida: debe ser 1 (Alta), 2 (Media) o 3 (Baja)");
    }
}