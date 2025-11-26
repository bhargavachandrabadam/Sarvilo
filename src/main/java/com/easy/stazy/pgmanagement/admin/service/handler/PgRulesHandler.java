package com.easy.stazy.pgmanagement.admin.service.handler;

import com.easy.stazy.pgmanagement.admin.dto.request.PgManagementRequestDto;
import com.easy.stazy.pgmanagement.pg.dto.response.PgDetailsResponseDto;
import com.easy.stazy.pgmanagement.pg.entities.PgManagementEntity;
import com.easy.stazy.pgmanagement.pg.entities.RuleEntity;
import com.easy.stazy.pgmanagement.pg.repository.RuleRepository;

import java.util.List;
import java.util.ArrayList;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PgRulesHandler implements PgFieldHandler {
    private final RuleRepository ruleRepository;

    @Override
    public void update(PgManagementRequestDto dto, PgManagementEntity entity) {
        if (dto.getRules() != null) {
            List<String> incomingRuleNames = dto.getRules();
            List<RuleEntity> updatedRules = new ArrayList<>();
            if (entity.getRules() != null) {
                updatedRules.addAll(entity.getRules().stream()
                        .filter(r -> incomingRuleNames.contains(r.getName()))
                        .toList());
            }
            for (String name : incomingRuleNames) {
                boolean exists = updatedRules.stream().anyMatch(r -> r.getName().equals(name));
                if (!exists) {
                    RuleEntity rule = entity.getRules() != null ? entity.getRules().stream()
                            .filter(r -> r.getName().equals(name))
                            .findFirst()
                            .orElse(null) : null;
                    if (rule == null) {
                        rule = new RuleEntity();
                        rule.setName(name);
                        rule.setPg(entity); // Set parent PG
                        ruleRepository.save(rule);
                    }
                    updatedRules.add(rule);
                }
            }
            if (entity.getRules() == null) {
                entity.setRules(new ArrayList<>());
            }
            entity.getRules().clear();
            entity.getRules().addAll(updatedRules);
        }
    }

    @Override
    public void handle(PgManagementEntity entity, PgDetailsResponseDto dto) {
        if (entity.getRules() != null) {
            dto.setRules(entity.getRules().stream().map(r -> r.getName()).toList());
        }
    }
}
