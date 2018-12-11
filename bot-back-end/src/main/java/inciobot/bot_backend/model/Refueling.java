package inciobot.bot_backend.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import inciobot.bot_backend.constants.ITableNames;
import inciobot.bot_backend.enums.RefuelingType;

@Entity
@Table(name = ITableNames.REFUELING)
public class Refueling {
	private Long id;
	private Date date;
	private Double price, distance, fuelQuantity;
	private RefuelingType type;

	@Id
	@GeneratedValue
	@Column(name = "fuel_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getFuelQuantity() {
		return fuelQuantity;
	}

	public void setFuelQuantity(Double fuelQuantity) {
		this.fuelQuantity = fuelQuantity;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "fuel_type")
	public RefuelingType getType() {
		return type;
	}

	public void setType(RefuelingType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder retval = new StringBuilder();
		retval.append("Type: " + type.name() + "\n");
		retval.append("Date: " + new SimpleDateFormat("dd-MM-YYYY").format(date) + "\n");
		retval.append("Price: " + price + "\n");
		retval.append("Quantity: " + fuelQuantity + "\n");
		retval.append("Distance: " + distance + "\n");
		return retval.toString();
	}

}
