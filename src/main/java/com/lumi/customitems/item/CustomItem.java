package com.lumi.customitems.item;

import java.util.List;
import java.util.Map;

public record CustomItem(
        String id,
        String material,
        String name,
        List<String> lore,
        int customModelData,
        boolean unbreakable,
        boolean glow,
        Map<String, Double> attributes,
        Map<String, Integer> enchants,
        Map<String, Object> abilities
) {}

