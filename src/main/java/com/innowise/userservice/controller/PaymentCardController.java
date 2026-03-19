package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.PaymentCardDto;
import com.innowise.userservice.model.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/payment-cards", produces = "application/json")
@RequiredArgsConstructor
public class PaymentCardController {

    private static final int PAGINATION_SIZE = 15;
    private static final String SORT_BY = "id";

    private final PaymentCardService paymentCardService;

    @PostMapping
    public ResponseEntity<PaymentCardDto> addPaymentCard(@Valid @RequestBody PaymentCardDto paymentCardDto) {
        PaymentCardDto paymentCard = paymentCardService.createPaymentCard(paymentCardDto);
        return new ResponseEntity<>(paymentCard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDto> getPaymentCardById(@PathVariable Long id) {

        PaymentCardDto paymentCard = paymentCardService.getPaymentCardById(id);

        return ResponseEntity.ok(paymentCard);
    }

    @GetMapping("/cards-by-user-id/{id}")
    public ResponseEntity<List<PaymentCardDto>> getAllPaymentCardsByUserId(@PathVariable Long id) {
        List<PaymentCardDto> paymentCards = paymentCardService.getAllPaymentCardsByUserId(id);
        return ResponseEntity.ok(paymentCards);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardDto>> getAllPaymentCards(
            @RequestParam(required = false) String number,
            @PageableDefault(size = PAGINATION_SIZE, sort = SORT_BY) Pageable pageable) {

        Page<PaymentCardDto> paymentCardsPage = paymentCardService.getAllPaymentCards(number, pageable);

        return ResponseEntity.ok(paymentCardsPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentCardDto> updatePaymentCard(@PathVariable Long id, @Valid @RequestBody PaymentCardDto paymentCardDto) {
        paymentCardDto.setId(id);

        PaymentCardDto paymentCard = paymentCardService.updatePaymentCard(paymentCardDto);

        return ResponseEntity.ok(paymentCard);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<PaymentCardDto> activatePaymentCard(@PathVariable Long id){
        boolean success = paymentCardService.activatePaymentCardById(id);

        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PaymentCardDto> deactivatePaymentCard(@PathVariable Long id){
        boolean success = paymentCardService.deactivatePaymentCardById(id);

        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
