package com.example.demo;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.time.Duration;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class TrainController {
    @GetMapping("/shapes")
    public ResponseEntity<Map<String, Object>> getShapes() throws Exception {
        return ResponseEntity.ok(trainService.getShapesWithRoutes());
    }

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
            .build();

    @GetMapping("/trains")
    public ResponseEntity<List<TrainUpdate>> getTrains() throws Exception {
        if (bucket.tryConsume(1)) {
            List<TrainUpdate> data = trainService.getLiveTrainData();
            return ResponseEntity.ok(data);
        }
        return ResponseEntity.status(429).body(null);
    }
}