package io.github.bulletsentence.springapi.controllers;

import com.fasterxml.jackson.databind.util.BeanUtil;
import io.github.bulletsentence.springapi.dtos.ParkingSpotDTO;
import io.github.bulletsentence.springapi.models.ParkingSpotModel;
import io.github.bulletsentence.springapi.services.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
        public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO) {

        if (parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate");
        }

        if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Number");
        }

        if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Apartment");
        }

        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots() {
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }

}
