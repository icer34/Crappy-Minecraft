package utils;

import blocks.BlockType;
import org.joml.Vector3f;
import org.joml.Vector3i;

public record RayCastResult(boolean hit, Vector3i targetPos, BlockType targetType, Vector3i targetNorm) {}
