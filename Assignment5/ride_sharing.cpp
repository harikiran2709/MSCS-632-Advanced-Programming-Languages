#include <iostream>
#include <string>
#include <vector>
#include <memory>
#include <iomanip>

// Ride base class demonstrating encapsulation and polymorphism via virtual methods
class Ride {
protected:
	std::string rideId;
	std::string pickupLocation;
	std::string dropoffLocation;
	double distanceMiles;

public:
	Ride(const std::string &id, const std::string &pickup, const std::string &dropoff, double distance)
		: rideId(id), pickupLocation(pickup), dropoffLocation(dropoff), distanceMiles(distance) {}
	virtual ~Ride() = default;

	virtual double fare() const {
		// Base fare: $2.00 + $1.50 per mile (can be overridden)
		return 2.0 + 1.5 * distanceMiles;
	}

	virtual std::string type() const { return "Ride"; }

	virtual void rideDetails(std::ostream &out) const {
		out << type() << "[" << rideId << "]: "
			<< pickupLocation << " -> " << dropoffLocation
			<< ", distance=" << std::fixed << std::setprecision(1) << distanceMiles << " mi"
			<< ", fare=$" << std::fixed << std::setprecision(2) << fare();
	}
};

// StandardRide overrides fare()
class StandardRide : public Ride {
public:
	using Ride::Ride;
	std::string type() const override { return "StandardRide"; }
	double fare() const override {
		// Standard: $2.00 base + $1.25 per mile
		return 2.0 + 1.25 * distanceMiles;
	}
};

// PremiumRide overrides fare(), costing more per mile
class PremiumRide : public Ride {
public:
	using Ride::Ride;
	std::string type() const override { return "PremiumRide"; }
	double fare() const override {
		// Premium: $3.50 base + $2.25 per mile
		return 3.5 + 2.25 * distanceMiles;
	}
};

// Driver class with encapsulated assignedRides
class Driver {
private:
	std::string driverId;
	std::string name;
	double rating; // 0-5
	std::vector<std::shared_ptr<Ride>> assignedRides; // encapsulated

public:
	Driver(const std::string &id, const std::string &driverName, double driverRating)
		: driverId(id), name(driverName), rating(driverRating) {}

	void addRide(const std::shared_ptr<Ride> &ride) {
		assignedRides.push_back(ride);
	}

	void getDriverInfo(std::ostream &out) const {
		out << "Driver[" << driverId << "]: " << name << ", rating=" << std::fixed << std::setprecision(1) << rating << "\n";
		out << "  Rides completed: " << assignedRides.size() << "\n";
		for (const auto &r : assignedRides) {
			out << "    - ";
			r->rideDetails(out);
			out << "\n";
		}
	}
};

// Rider class maintains requested rides
class Rider {
private:
	std::string riderId;
	std::string name;
	std::vector<std::shared_ptr<Ride>> requestedRides; // encapsulated

public:
	Rider(const std::string &id, const std::string &riderName)
		: riderId(id), name(riderName) {}

	void requestRide(const std::shared_ptr<Ride> &ride) {
		requestedRides.push_back(ride);
	}

	void viewRides(std::ostream &out) const {
		out << "Rider[" << riderId << "]: " << name << "\n";
		out << "  Requested rides: " << requestedRides.size() << "\n";
		for (const auto &r : requestedRides) {
			out << "    - ";
			r->rideDetails(out);
			out << "\n";
		}
	}
};

int main() {
	// Create rides of different types
	std::vector<std::shared_ptr<Ride>> rides;
	rides.push_back(std::make_shared<StandardRide>("R001", "Campus", "Downtown", 5.0));
	rides.push_back(std::make_shared<PremiumRide>("R002", "Airport", "Hotel", 12.3));
	rides.push_back(std::make_shared<StandardRide>("R003", "Mall", "Stadium", 8.7));

	// Demonstrate polymorphism: calling fare() and rideDetails() via base pointer
	std::cout << "All Rides (polymorphic details):\n";
	for (const auto &r : rides) {
		r->rideDetails(std::cout);
		std::cout << "\n";
	}
	std::cout << "\n";

	// Create a driver and assign rides
	Driver driver("D100", "Alex Morgan", 4.8);
	for (const auto &r : rides) driver.addRide(r);
	
	// Create a rider and request two rides
	Rider rider("U200", "Taylor Kim");
	rider.requestRide(rides[0]);
	rider.requestRide(rides[2]);

	// Display info
	driver.getDriverInfo(std::cout);
	std::cout << "\n";
	rider.viewRides(std::cout);

	return 0;
}


