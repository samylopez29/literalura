package com.aluracursos.literAlura.principal;

public interface IConversor {
    <T> T obtenerDatos(String json, Class <T> clase);
}
