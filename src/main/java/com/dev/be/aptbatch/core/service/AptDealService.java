package com.dev.be.aptbatch.core.service;

import com.dev.be.aptbatch.core.dto.AptDealDto;
import com.dev.be.aptbatch.core.entity.Apt;
import com.dev.be.aptbatch.core.entity.AptDeal;
import com.dev.be.aptbatch.core.repository.AptDealRepository;
import com.dev.be.aptbatch.core.repository.AptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AptDealDto 에 있는 값을 Apt, AptDeal 엔티티로 저장
 */
@Service
@AllArgsConstructor
public class AptDealService {

    private final AptRepository aptRepository;
    private final AptDealRepository aptDealRepository;

    @Transactional
    public void upsert(AptDealDto aptDealDto) {
        Apt apt = getAptOrNew(aptDealDto);
        saveAptDeal(aptDealDto, apt);
    }

    private Apt getAptOrNew(AptDealDto aptDealDto) {
        Apt apt = aptRepository.findAptByAptNameAndJibun(aptDealDto.getAptName(), aptDealDto.getJibun())
                .orElseGet(() -> Apt.from(aptDealDto));
        return aptRepository.save(apt);
    }

    private void saveAptDeal(AptDealDto dto, Apt apt) {
        AptDeal aptDeal = aptDealRepository.findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
                        apt, dto.getExclusiveArea(),dto.getDealDate(), dto.getDealAmount(), dto.getFloor())
                .orElseGet(() -> AptDeal.of(dto, apt));
        aptDeal.setDealCanceled(dto.isDealCanceled());
        aptDeal.setDealCanceledDate(dto.getDealCanceledDate());
        aptDealRepository.save(aptDeal);
    }


}
