package com.team.discovery.pas_socialization2_backend.service.impl;

import com.team.discovery.pas_socialization2_backend.controller.model.Cotizar;
import com.team.discovery.pas_socialization2_backend.controller.model.Despacho;
import com.team.discovery.pas_socialization2_backend.model.despachos_db.Offer;
import com.team.discovery.pas_socialization2_backend.model.despachos_db.Shipping;
import com.team.discovery.pas_socialization2_backend.model.despachos_db.State;
import com.team.discovery.pas_socialization2_backend.model.despachos_db.User;
import com.team.discovery.pas_socialization2_backend.repository.DispatchRepository;
import com.team.discovery.pas_socialization2_backend.repository.OfferRepository;
import com.team.discovery.pas_socialization2_backend.repository.ShippingRepository;
import com.team.discovery.pas_socialization2_backend.service.IDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DispatchService implements IDispatchService {
    private DispatchRepository dispatchRepository;
    private OfferRepository offerRepository;
    private ShippingRepository shippingRepository;
    private MailService mailService;

    public DispatchService(DispatchRepository dispatchRepository, OfferRepository offerRepository, ShippingRepository shippingRepository, MailService mailService) {
        this.dispatchRepository = dispatchRepository;
        this.offerRepository = offerRepository;
        this.shippingRepository = shippingRepository;
        this.mailService = mailService;
    }

    @Override
    public List<Despacho> searchDispatches(State state) {
        List<Shipping> shippings = dispatchRepository.findShippingByState(state);
        List<Despacho> despachos = new ArrayList<>();

        if (shippings != null){
            for (Shipping shipping : shippings){
                Despacho despacho = new Despacho();
                despacho.setId(shipping.getId() != null ?
                        shipping.getId().intValue() : null);
                despacho.setCantidadCajas(shipping.getBoxesAmount() != null ?
                        shipping.getBoxesAmount().intValue() : null);
                despacho.setPesoTotal(shipping.getTotalWeight() != null ?
                        shipping.getTotalWeight().intValue() : null);
                despacho.setIdEstado(getStateID(shipping.getState()));
                despacho.setIdUsuarioDestino(shipping.getUser().getId() != null ?
                        shipping.getUser().getId().intValue() : null);
                despacho.setMejorOferta(shipping.getBestOffer() != null ?
                        shipping.getBestOffer().intValue() : null);
                despacho.setTransportadora(shipping.getDispatcher());
                despachos.add(despacho);
            }
        }

        return despachos;
    }

    @Override
    public void createOffer(long id, Cotizar cotizar) {
        Shipping shipping = new Shipping();
        shipping.setId(id);
        User usuarioTransporte = new User();
        usuarioTransporte.setId(cotizar.getIdUsuarioTransporte().longValue());
        Offer offerSave = Offer.builder().shipping(shipping).userTransport(usuarioTransporte).value(cotizar.getOferta()).build();
        offerRepository.save(offerSave);
        log.info("Successful Offer creation");

        List<Offer> offers = offerRepository.findAll().stream().filter(offer -> offer.getShipping().getId().intValue() == id).collect(Collectors.toList());
        shipping.setBestOffer(0L);
        for (Offer offer : offers){
            if(offer.getValue() > shipping.getBestOffer()){
                shipping = offer.getShipping();
                shipping.setState(State.DESPACHO_POR_APROBAR);
                shipping.setBestOffer(offer.getValue().longValue());
                shipping.setDispatcher(offer.getUserTransport().getName());
            }
        }
        shippingRepository.save(shipping);
        log.info("Successful Shipping update");

        List<Shipping> shippings = shippingRepository.findAll().stream().filter(shipping1 -> shipping1.getId().intValue() == id).collect(Collectors.toList());

        try {
            mailService.send(shippings.get(0).getUser().getEmail(), "Completa tu Despacho.");
            log.info("Envio de test Correcto.");
        }
        catch(Exception e){
            log.info("Excepción controlada, normal en el entorno de test",e);
        }

    }

    public State getStateEnum(int id) {
        switch (id) {
            case 1:
                return State.DESPACHO_CARGADO;
            case 2:
                return State.DESPACHO_OFERTADO;
            case 3:
                return State.DESPACHO_POR_APROBAR;
            case 4:
                return State.DESPACHO_APROBADO;
            case 5:
                return State.DESPACHO_RECHAZADO;
            default:
                return State.DESPACHO_ENVIADO;
        }

    }

    public int getStateID(State stateEnum) {
        switch (stateEnum) {
            case DESPACHO_CARGADO:
                return 1;
            case DESPACHO_OFERTADO:
                return 2;
            case DESPACHO_POR_APROBAR:
                return 3;
            case DESPACHO_APROBADO:
                return 4;
            case DESPACHO_RECHAZADO:
                return 5;
            default:
                return 6;
        }

    }
}
