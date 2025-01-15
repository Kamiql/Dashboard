package de.kamiql.Dashboard.spring.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class AvatarService {
    public BufferedImage generateAvatar(String name) {
        String initials = name.length() > 1
                ? name.substring(0, 1).toUpperCase() + name.chars()
                    .skip(1)
                    .filter(Character::isUpperCase)
                    .mapToObj(c -> String.valueOf((char) c))
                    .findFirst()
                    .orElse("")
                : name.substring(0, 1).toUpperCase();

        int size = 100;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, size, size);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, size / 2));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (size - fm.stringWidth(initials)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();

        g2d.drawString(initials, x, y);
        g2d.dispose();

        return image;
    }
}
