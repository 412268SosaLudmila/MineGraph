package com.minegraph.service;

import com.minegraph.dto.response.ClanResponse;
import com.minegraph.mapper.ClanMapper;
import com.minegraph.repository.ClanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClanService {

    private final ClanRepository clanRepository;
    private final ClanMapper mapper;

    public ClanService(ClanRepository clanRepository, ClanMapper mapper) {
        this.clanRepository = clanRepository;
        this.mapper = mapper;
    }

    public List<ClanResponse> findAll() {
        return mapper.toResponseList(clanRepository.findAllByOrderByRankingAsc());
    }

    public Optional<ClanResponse> findById(Long id) {
        return clanRepository.findById(id).map(mapper::toResponse);
    }

    public List<ClanResponse> findDominantes(int limit) {
        return mapper.toResponseList(clanRepository.findClanesDominantes(limit));
    }

    public List<ClanResponse> findEnGuerra() {
        return mapper.toResponseList(clanRepository.findClanesEnGuerra());
    }

    public List<ClanResponse> findTopPorTerritorio(int limit) {
        return mapper.toResponseList(clanRepository.findTopPorTerritorio(limit));
    }

    public List<ClanResponse> findNucleoComunidad() {
        return mapper.toResponseList(clanRepository.findClanesNucleoComunidad());
    }

    public Long countTotal() {
        return clanRepository.countTotal();
    }
}
