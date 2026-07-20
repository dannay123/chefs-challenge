package com.chefchallenge.server.controller;

import com.chefchallenge.server.model.dto.*;
import com.chefchallenge.server.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "*")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // ── Start ─────────────────────────────────────────────────────────────────

    @RequestMapping(value = "/start", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<StartSessionResponse> start() {
        return ResponseEntity.ok(sessionService.startSession());
    }

    // ── Hint ──────────────────────────────────────────────────────────────────

    @PostMapping("/{sessionId}/hint")
    public ResponseEntity<?> hint(@PathVariable String sessionId,
                                  @RequestBody HintRequest request) {
        return handleHint(sessionId, request.getHintType());
    }

    @GetMapping("/{sessionId}/hint")
    public ResponseEntity<?> hintGet(@PathVariable String sessionId,
                                     @RequestParam(defaultValue = "INGREDIENT") String hintType) {
        return handleHint(sessionId, hintType);
    }

    private ResponseEntity<?> handleHint(String sessionId, String hintType) {
        try {
            return ResponseEntity.ok(sessionService.requestHint(sessionId, hintType));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    @PostMapping("/{sessionId}/submit")
    public ResponseEntity<?> submit(@PathVariable String sessionId,
                                    @RequestBody SubmitRequest request) {
        return handleSubmit(sessionId, request);
    }

    @GetMapping("/{sessionId}/submit")
    public ResponseEntity<?> submitGet(@PathVariable String sessionId,
                                       @RequestParam(defaultValue = "") List<String> ingredients,
                                       @RequestParam(defaultValue = "") List<String> techniques) {
        SubmitRequest request = new SubmitRequest();
        request.setIngredients(ingredients);
        request.setTechniques(techniques);
        return handleSubmit(sessionId, request);
    }

    private ResponseEntity<?> handleSubmit(String sessionId, SubmitRequest request) {
        try {
            return ResponseEntity.ok(sessionService.submitDish(sessionId, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // ── Timeout ───────────────────────────────────────────────────────────────

    @RequestMapping(value = "/{sessionId}/timeout", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> timeout(@PathVariable String sessionId) {
        try {
            return ResponseEntity.ok(sessionService.handleTimeout(sessionId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
