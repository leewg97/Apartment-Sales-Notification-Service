package com.dev.be.aptbatch.core.service;

import com.dev.be.aptbatch.core.entity.Lawd;
import com.dev.be.aptbatch.core.repository.LawdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LawdService {

    private final LawdRepository lawdRepository;

    /**
     * 데이터가 존재하면 수정, 없으면 생성한다.
     * @param lawd
     */
    public void upsert(Lawd lawd) {
        Lawd saved = lawdRepository.findByLawdCd(lawd.getLawdCd()).orElseGet(Lawd::new);
        saved.setLawdCd(lawd.getLawdCd());
        saved.setLawdDong(lawd.getLawdDong());
        saved.setExist(lawd.getExist());
        lawdRepository.save(saved);
    }


}
