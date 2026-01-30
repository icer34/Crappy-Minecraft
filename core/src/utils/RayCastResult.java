package utils;

import org.joml.Vector3i;

public record RayCastResult(boolean hit, Vector3i targetPos, int targetID, Vector3i targetNorm) {}
