package nz.ac.auckland.se206.controllers;

public class Visitor {

  private String name;
  private int checkinTime;
  private int checkoutTime;
  private String host;

  public Visitor(String name, int checkinTime, int checkoutTime, String host){
    this.name = name;
    this.checkinTime = checkinTime;
    this.checkoutTime = checkoutTime;
    this.host = host;
  }

  public String getName(){
    return name;
  }

  public int getCheckinTime(){
    return checkinTime;
  }

  public int getCheckoutTime(){
    return checkoutTime;
  }

  public String getHost(){
    return host;
  }
}