package com.dev.be.aptbatch.job.notify;

import com.dev.be.aptbatch.BatchTestConfig;
import com.dev.be.aptbatch.adapter.FakeSendService;
import com.dev.be.aptbatch.core.dto.AptDto;
import com.dev.be.aptbatch.core.entity.AptNotification;
import com.dev.be.aptbatch.core.entity.Lawd;
import com.dev.be.aptbatch.core.repository.AptNotificationRepository;
import com.dev.be.aptbatch.core.repository.LawdRepository;
import com.dev.be.aptbatch.core.service.AptDealService;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {AptNotificationJobConfig.class, BatchTestConfig.class})
public class AptNotificationJobConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private AptNotificationRepository aptNotificationRepository;
    @MockBean
    private AptDealService aptDealService;
    @MockBean
    private LawdRepository lawdRepository;
    @MockBean
    private FakeSendService fakeSendService;
    @AfterEach
    public void tearDown() {
        aptNotificationRepository.deleteAll();
    }

    @Test
    public void success() throws Exception {
        // given
        LocalDate dealDate = LocalDate.now().minusDays(1);
        String guLawdCd = "11110";
        String email = "aaa@aaa.com";
        String anotherEmail = "bbb@bbb.com";

        givenAptNotification(guLawdCd, email, true);
        givenAptNotification(guLawdCd, anotherEmail, false);
        givenLawdCd(guLawdCd);
        givenAptDeal(guLawdCd, dealDate);
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParameters(Maps.newHashMap("dealDate", new JobParameter(dealDate.toString())))
        );

        // then
        assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        verify(fakeSendService, times(1)).send(eq(email), anyString());
        verify(fakeSendService, never()).send(eq(anotherEmail), anyString());

    }

    private void givenAptNotification(String guLawdCd, String email, boolean enabled) {
        AptNotification notification = new AptNotification();
        notification.setEmail(email);
        notification.setGuLawdCd(guLawdCd);
        notification.setEnabled(enabled);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        aptNotificationRepository.save(notification);
    }


    private void givenLawdCd(String guLawdCd) {
        String lawdCd = guLawdCd + "00000";
        Lawd lawd = new Lawd();
        lawd.setLawdCd(lawdCd);
        lawd.setLawdDong("서울시 서초구 반포동");
        lawd.setExist(true);
        lawd.setCreatedAt(LocalDateTime.now());
        lawd.setUpdatedAt(LocalDateTime.now());
        when(lawdRepository.findByLawdCd(lawdCd)).thenReturn(Optional.of(lawd));
    }

    private void givenAptDeal(String guLawdCd, LocalDate dealDate) {
        when(aptDealService.findByGuLawdCdAndDealDate(guLawdCd, dealDate))
                .thenReturn(Arrays.asList(
                        new AptDto("반포주공1단지", 5300000000L),
                        new AptDto("궁전", 2300000000L)
                ));
    }

}
