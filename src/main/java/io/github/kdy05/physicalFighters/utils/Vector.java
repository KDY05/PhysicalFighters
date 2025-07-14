package io.github.kdy05.physicalFighters.utils;

import org.bukkit.Location;

@SuppressWarnings("unused")
public class Vector {
    private double x;
    private double y;
    private double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Location l) {
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
    }

    public double getX() {
        return this.x;
    }

    public int getBlockX() {
        return (int) Math.round(this.x);
    }

    public Vector setX(double x) {
        this.x = x;
        return new Vector(x, this.y, this.z);
    }

    public Vector setX(int x) {
        this.x = x;
        return new Vector(x, this.y, this.z);
    }

    public double getY() {
        return this.y;
    }

    public int getBlockY() {
        return (int) Math.round(this.y);
    }

    public Vector setY(double y) {
        this.y = y;
        return new Vector(this.x, y, this.z);
    }

    public Vector setY(int y) {
        this.y = y;
        return new Vector(this.x, y, this.z);
    }

    public double getZ() {
        return this.z;
    }

    public int getBlockZ() {
        return (int) Math.round(this.z);
    }

    public Vector setZ(double z) {
        this.z = z;
        return new Vector(this.x, this.y, z);
    }

    public Vector setZ(int z) {
        this.z = z;
        return new Vector(this.x, this.y, z);
    }

    public double distance(Vector pt) {
        return Math.sqrt(Math.pow(pt.x - this.x, 2.0D) +
                        Math.pow(pt.y - this.y, 2.0D) +
                        Math.pow(pt.z - this.z, 2.0D));
    }

    public double distance(Location l) {
        return Math.sqrt(Math.pow(l.getX() - this.x, 2.0D) +
                Math.pow(l.getY() - this.y, 2.0D) +
                Math.pow(l.getZ() - this.z, 2.0D));
    }

    public double distance(double xx, double yy, double zz) {
        return Math.sqrt(Math.pow(xx - this.x, 2.0D) +
                Math.pow(yy - this.y, 2.0D) + Math.pow(zz - this.z, 2.0D));
    }
}
