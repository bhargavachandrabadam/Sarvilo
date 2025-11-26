package com.easy.stazy.shared.shorturl.service;

import com.easy.stazy.shared.shorturl.entities.ShortUrlEntity;
import com.easy.stazy.shared.shorturl.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ShortUrlService {
    private static final AtomicLong COUNTER = new AtomicLong(1000000000000L); // Start from a large number for shorter codes

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final ShortUrlRepository shortUrlRepository;

    private String encodeBase62(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % 62);
            sb.append(BASE62.charAt(remainder));
            value /= 62;
        }
        return sb.reverse().toString();
    }

    @Transactional
    public String createShortUrl(String originalUrl) {
        ShortUrlEntity entity = new ShortUrlEntity();
        entity.setOriginalUrl(originalUrl);
        entity.setCode("");
        entity = shortUrlRepository.save(entity);
        long id = entity.getId();
        long counter = COUNTER.getAndIncrement();
        String idBase62 = encodeBase62(id);
        String counterBase62 = encodeBase62(counter);
        String code = getString(idBase62, counterBase62);
        entity.setCode(code);
        shortUrlRepository.save(entity);
        return code;
    }

    private static String getString(String idBase62, String counterBase62) {
        StringBuilder mixed = new StringBuilder();
        int maxLen = Math.max(idBase62.length(), counterBase62.length());
        for (int i = 0; i < maxLen; i++) {
            if (i < idBase62.length()) mixed.append(idBase62.charAt(i));
            if (i < counterBase62.length()) mixed.append(counterBase62.charAt(i));
        }
        return mixed.length() >= 7 ? mixed.substring(0, 7) : String.format("%1$" + 7 + "s", mixed).replace(' ', '0');
    }

    public Optional<String> getOriginalUrl(String code) {
        return shortUrlRepository.findByCode(code).map(ShortUrlEntity::getOriginalUrl);
    }
}
