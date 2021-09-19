package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.PlayerDTO;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List <Player> getPlayers(PlayerDTO searchParams) {
        //set default values
        String name = searchParams.getName();
        String title = searchParams.getTitle();
        Race race = searchParams.getRace();
        Profession profession = searchParams.getProfession();
        Long after = searchParams.getAfter();
        Long before = searchParams.getBefore();
        Boolean banned = searchParams.getBanned();
        Long minExp = searchParams.getMinExperience();
        Long maxExp = searchParams.getMaxExperience();
        Long minLevel = searchParams.getMinLevel();
        Long maxLevel = searchParams.getMaxLevel();
        //PlayerOrder order = searchParams.getPlayerOrder();

        Long pageNumber = searchParams.getPageNumber();
        Long pageSize = searchParams.getPageSize();

        //pageable
        Pageable pageable = PageRequest.of(pageNumber.intValue(), pageSize.intValue());

        return playerRepository.findAll(PlayerSpecification.findPlayerByName(name)
                .and(PlayerSpecification.findPlayerByRace(race))
                .and(PlayerSpecification.findPlayerByProfession(profession))
                .and(PlayerSpecification.findPlayerByStatus(banned))
                .and(PlayerSpecification.findPlayerByTitle(title))
                .and(PlayerSpecification.findPlayerByDate(after, before))
                .and(PlayerSpecification.findPlayerByExp(minExp, maxExp))
                .and(PlayerSpecification.findPlayerByMaxLevel(maxLevel))
                .and(PlayerSpecification.findPlayerByMinLevel(minLevel)), pageable).getContent();
    }

    public Player createPlayer(Player player) {
        //invalid body
        String name = player.getName();
        String title = player.getTitle();
        Race race = player.getRace();
        Profession profession = player.getProfession();
        Date birthdayDate = player.getBirthday();
        Integer exp = player.getExperience();

        if(name == null || title == null || race == null || profession == null || birthdayDate == null || exp == null) {
            return null;
        }

        //invalid data
        if((name.length() > 12 || name.isEmpty()) || title.length() > 30) {
            return null;
        }

        //exp
        if(exp < 0 || exp > 10e6) {
            return null;
        }

        //birthday
        Integer year = birthdayDate.getYear() + 1900;
        if(birthdayDate.getTime() < 0 || year > 3000 || year < 2000) {
            return null;
        }

        Integer level = player.getCurrentLevel();
        player.setLevel(level);

        Integer nextLvlExp = player.getNextLevelExperience();
        player.setUntilNextLevel(nextLvlExp);

        return playerRepository.save(player);
    }

    public Long getPlayerCount(PlayerDTO searchParam) {
        return (long) playerRepository.findAll(PlayerSpecification.findPlayerByName(searchParam.getName())
                .and(PlayerSpecification.findPlayerByRace(searchParam.getRace()))
                .and(PlayerSpecification.findPlayerByProfession(searchParam.getProfession()))
                .and(PlayerSpecification.findPlayerByStatus(searchParam.getBanned()))
                .and(PlayerSpecification.findPlayerByTitle(searchParam.getTitle()))
                .and(PlayerSpecification.findPlayerByDate(searchParam.getAfter(), searchParam.getBefore()))
                .and(PlayerSpecification.findPlayerByExp(searchParam.getMinExperience(), searchParam.getMaxExperience()))
                .and(PlayerSpecification.findPlayerByMaxLevel(searchParam.getMaxLevel()))
                .and(PlayerSpecification.findPlayerByMinLevel(searchParam.getMinLevel()))).size();
        //return playerRepository.count();
    }

    public Player getPlayer(Long id) {
        return playerRepository.findById(id).get();
    }

    public Boolean playerExists(Long id) {
        return playerRepository.existsById(id);
    }

    public ResponseEntity<Player> removePlayer(String removedId) {
        Long id = 0L;
        try {
            id = Long.parseLong(removedId);
        } catch (NumberFormatException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (!playerExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            playerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    public ResponseEntity<Player> updatePlayer(String playerId, Player playerToUpdate) {
        Long id = Long.valueOf(playerId);
        if(id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(!playerRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //data
        String name = playerToUpdate.getName();
        String title = playerToUpdate.getTitle();
        Race race = playerToUpdate.getRace();
        Profession profession = playerToUpdate.getProfession();
        Date birthdayDate = playerToUpdate.getBirthday();
        Integer exp = playerToUpdate.getExperience();
        Boolean banned = playerToUpdate.getBanned();
        Integer level = playerToUpdate.getLevel();

        Player existingPlayer = playerRepository.findById(id).get();
        //if body is empty - nothing to update
        if(name == null && title == null && race == null && profession == null && birthdayDate == null && exp == null && banned == null && level == null) {
            return new ResponseEntity<>(existingPlayer, HttpStatus.OK);
        }

        if(name != null) {
            existingPlayer.setName(name);
        }

        if(title != null) {
            existingPlayer.setTitle(title);
        }

        if(birthdayDate != null) {
            int year = birthdayDate.getYear() + 1900;
            if (birthdayDate.getTime() < 0 || year > 3000 || year < 2000) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingPlayer.setBirthday(birthdayDate);
        }

        if(race != null) {
            existingPlayer.setRace(race);
        }

        if(profession != null) {
            existingPlayer.setProfession(profession);
        }

        if(banned != null) {
            existingPlayer.setBanned(banned);
        }

        if(exp != null) {
            if (!(exp < 0 || exp > 10e6)) {
                existingPlayer.setExperience(exp);
                Integer currentLevel = existingPlayer.getCurrentLevel();
                existingPlayer.setLevel(currentLevel);

                Integer nextLvlExp = existingPlayer.getNextLevelExperience();
                existingPlayer.setUntilNextLevel(nextLvlExp);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        Player updatedPlayer = playerRepository.save(existingPlayer);
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }
}
