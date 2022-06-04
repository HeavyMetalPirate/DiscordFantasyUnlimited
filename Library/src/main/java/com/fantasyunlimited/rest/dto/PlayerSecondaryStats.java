package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.data.entity.SecondarySkills;

public record PlayerSecondaryStats(
   SecondarySkills playerSkills,
   SecondarySkills equipmentSkills
) {}
