package com.example.healthylife.service;

import com.example.healthylife.entity.HeartEntity;
import com.example.healthylife.entity.TodayEntity;
import com.example.healthylife.entity.UserEntity;
import com.example.healthylife.repository.HeartRepository;
import com.example.healthylife.repository.TodayRepository;
import com.example.healthylife.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class HeartService {

    private final HeartRepository heartRepository;
    private final TodayRepository todayRepository;
    private final UserRepository userRepository;

    @Transactional
    public long toggleLike(Long userSq, Long todaySq) {
        TodayEntity today = todayRepository.findById(todaySq)
                .orElseThrow(() -> new RuntimeException("투데이가 없습니다."));
        UserEntity user = userRepository.findById(userSq)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));

        boolean alreadyHeart = heartRepository.existsByUserAndToday(user,today);

        if(alreadyHeart) {
            HeartEntity heart = heartRepository.findByUserAndToday(user,today)
                    .orElseThrow(() -> new RuntimeException("추천 기록이 없습니다."));
            heartRepository.delete(heart);
            today.decrementLikeCount();
        } else {
            HeartEntity heart = new HeartEntity(today,user);
            heartRepository.save(heart);
            today.incrementLikeCount();
        }

        todayRepository.save(today);
        return today.getTodayHearts();
    }


    // 사용자가 특정 오늘의 글에 대해 좋아요를 눌렀는지 여부 확인
    public boolean hasUserLiked(Long todaySq, Long userSq) {
        // 오늘의 글 및 사용자 엔티티 조회
        TodayEntity today = todayRepository.findById(todaySq)
                .orElseThrow(() -> new RuntimeException("오늘의 글이 없습니다."));
        UserEntity user = userRepository.findById(userSq)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));

        return heartRepository.existsByUserAndToday(user, today);
    }
}
