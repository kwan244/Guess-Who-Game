package nz.ac.auckland.se206.controllers;

public class Visitor {

  private int id;
  private String name;
  private String checkinTime;
  private String checkoutTime;
  private String host;

  public Visitor(int id, String name, String checkinTime, String checkoutTime, String host) {
    this.id = id;
    this.name = name;
    this.checkinTime = checkinTime;
    this.checkoutTime = checkoutTime;
    this.host = host;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCheckinTime() {
    return checkinTime;
  }

  public String getCheckoutTime() {
    return checkoutTime;
  }

  public String getHost() {
    return host;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCheckinTime(String checkinTime) {
    this.checkinTime = checkinTime;
  }

  public void setCheckoutTime(String checkoutTime) {
    this.checkoutTime = checkoutTime;
  }

  public void setHost(String host) {
    this.host = host;
  }
}
