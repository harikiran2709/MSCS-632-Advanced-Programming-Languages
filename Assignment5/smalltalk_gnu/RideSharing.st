"GNU Smalltalk version - run with: gst RideSharing.st"

Object subclass: Ride [
    | rideId pickupLocation dropoffLocation distanceMiles |

    Ride class >> withId: anId pickup: pickup dropoff: dropoff distance: miles [
        ^ self new
            setId: anId
            pickup: pickup
            dropoff: dropoff
            distance: miles;
            yourself
    ]

    setId: anId pickup: pickup dropoff: dropoff distance: miles [
        rideId := anId.
        pickupLocation := pickup.
        dropoffLocation := dropoff.
        distanceMiles := miles.
    ]

    type [ ^ 'Ride' ]

    fare [ ^ 2.0 + (1.5 * distanceMiles) ]

    detailsString [
        | tenths intPart frac cents dollars centsR |
        tenths := (distanceMiles * 10) rounded.
        intPart := tenths // 10.
        frac := tenths \\ 10.
        cents := (self fare * 100) rounded.
        dollars := cents // 100.
        centsR := cents \\ 100.
        ^ String streamContents: [:s |
            s
                nextPutAll: self type; nextPutAll: '['; nextPutAll: rideId; nextPutAll: ']: ';
                nextPutAll: pickupLocation; nextPutAll: ' -> '; nextPutAll: dropoffLocation;
                nextPutAll: ', distance='; nextPutAll: intPart printString; nextPutAll: '.'; nextPutAll: frac printString; nextPutAll: ' mi';
                nextPutAll: ', fare=$'; nextPutAll: dollars printString; nextPutAll: '.';
                nextPutAll: (centsR < 10 ifTrue: ['0', centsR printString] ifFalse: [centsR printString]) ]
    ]
]

Ride subclass: StandardRide [
    type [ ^ 'StandardRide' ]
    fare [ ^ 2.0 + (1.25 * distanceMiles) ]
]

Ride subclass: PremiumRide [
    type [ ^ 'PremiumRide' ]
    fare [ ^ 3.5 + (2.25 * distanceMiles) ]
]

Object subclass: Driver [
    | driverId name rating assignedRides |

    Driver class >> withId: anId name: aName rating: aRating [
        ^ self new setId: anId name: aName rating: aRating; yourself
    ]

    setId: anId name: aName rating: aRating [
        driverId := anId.
        name := aName.
        rating := aRating.
        assignedRides := OrderedCollection new.
    ]

    addRide: aRide [ assignedRides add: aRide ]

    infoString [
        | tenths intPart frac |
        tenths := (rating * 10) rounded.
        intPart := tenths // 10.
        frac := tenths \\ 10.
        ^ String streamContents: [:s |
            s nextPutAll: 'Driver['; nextPutAll: driverId; nextPutAll: ']: ';
            nextPutAll: name; nextPutAll: ', rating='; nextPutAll: intPart printString; nextPutAll: '.'; nextPutAll: frac printString; nl.
            s nextPutAll: '  Rides completed: '; nextPutAll: assignedRides size printString; nl.
            assignedRides do: [:r | s nextPutAll: '    - '; nextPutAll: r detailsString; nl ] ]
    ]
]

Object subclass: Rider [
    | riderId name requestedRides |

    Rider class >> withId: anId name: aName [
        ^ self new setId: anId name: aName; yourself
    ]

    setId: anId name: aName [
        riderId := anId.
        name := aName.
        requestedRides := OrderedCollection new.
    ]

    requestRide: aRide [ requestedRides add: aRide ]

    ridesString [
        ^ String streamContents: [:s |
            s nextPutAll: 'Rider['; nextPutAll: riderId; nextPutAll: ']: ';
            nextPutAll: name; nl.
            s nextPutAll: '  Requested rides: '; nextPutAll: requestedRides size printString; nl.
            requestedRides do: [:r | s nextPutAll: '    - '; nextPutAll: r detailsString; nl ] ]
    ]
]

" Main demo "
| rides driver rider |

FileStream stdout nextPutAll: 'All Rides (polymorphic details):'; nl.

rides := OrderedCollection new.
rides add: (StandardRide withId: 'R001' pickup: 'Campus' dropoff: 'Downtown' distance: 5.0).
rides add: (PremiumRide withId: 'R002' pickup: 'Airport' dropoff: 'Hotel' distance: 12.3).
rides add: (StandardRide withId: 'R003' pickup: 'Mall' dropoff: 'Stadium' distance: 8.7).

rides do: [:r | FileStream stdout nextPutAll: r detailsString; nl ].
FileStream stdout nl.

driver := Driver withId: 'D100' name: 'Alex Morgan' rating: 4.8.
rides do: [:r | driver addRide: r ].

rider := Rider withId: 'U200' name: 'Taylor Kim'.
rider requestRide: rides first.
rider requestRide: rides last.

FileStream stdout nextPutAll: driver infoString; nl; nl.
FileStream stdout nextPutAll: rider ridesString; nl.
