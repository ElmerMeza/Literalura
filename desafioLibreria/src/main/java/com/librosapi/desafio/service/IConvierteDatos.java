package com.librosapi.desafio.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json,Class<T> Class);
}
