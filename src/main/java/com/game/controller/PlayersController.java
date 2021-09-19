package com.game.controller;

import com.game.entity.Player;
import com.game.entity.PlayerDTO;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/rest/players")
public class PlayersController {

    private final PlayerService playerService;

    @Autowired
    public PlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getPlayerList(PlayerDTO searchParams) {
        List<Player> players = playerService.getPlayers(searchParams);

        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player savedPlayer = playerService.createPlayer(player);

        if(savedPlayer == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(savedPlayer, HttpStatus.OK);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getPlayersCount(PlayerDTO playerDTO) {
        Long playersCount = playerService.getPlayerCount(playerDTO);
        return new ResponseEntity<>(playersCount, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id) {
        //Если id некорректный
        if(id == null || id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //Существует ли игрок
        if(!playerService.playerExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Player player = playerService.getPlayer(id);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable String id, @RequestBody Player player) {
        return playerService.updatePlayer(id, player);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Player> removePlayer(@PathVariable String id) {
        return playerService.removePlayer(id);
    }
}
