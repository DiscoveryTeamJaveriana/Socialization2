package com.team.discovery.pas_socialization2_backend.service;

import com.team.discovery.pas_socialization2_backend.controller.model.Usuario;

public interface IUserService {

    String createUser(Usuario requestUsuario);

    Usuario searchUser(String nombreUsuario);

}
