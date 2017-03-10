var self = this;
var rainbow = new Rainbow();
rainbow.setSpectrum('red', 'FFFFFF', '#00ff00');
rainbow.setNumberRange(0, 50);

self.getWebsocket = function () {
    var path = "ws://" + window.location.hostname + ":8001/ws/devices";
    return new WebSocket(path);
};

var center = new ol.Feature({
    //geometry: new ol.geom.Point(ol.proj.fromLonLat([-118.23194,34.12527]))
    id: "center",
    geometry: new ol.geom.Point(ol.proj.fromLonLat([12.2204636, 44.1285244]))
});

center.setStyle(new ol.style.Style({
    image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
        color: '#8959A8',
        src: 'data/dot.png'
    }))
}));


self.vehicleSource = new ol.source.Vector();
var vehicleLayer = new ol.layer.Vector({
    source: self.vehicleSource
});
vehicleLayer.setZIndex(6)


var rasterLayer = new ol.layer.Tile({
    source: new ol.source.OSM()
});


var map = new ol.Map({
    controls: ol.control.defaults().extend([
        new ol.control.OverviewMap()
    ]),
    target: 'map',
    layers: [rasterLayer, vehicleLayer],
    view: new ol.View({
        projection: ol.proj.get('EPSG:3857'),
        //34.05374 | -118.30814
        //center: ol.proj.fromLonLat([-118.23194,34.12527]),
        center: ol.proj.fromLonLat([12.2204636, 44.1285244]),
        zoom: 14
    })
});

self.akkaServiceBasis = 'http://' + window.location.hostname + ':8000/vehicles/boundingBox?bbox='


self.ajax = function (uri) {
    var request = {
        url: uri,
        type: 'GET',
        contentType: "application/json",
        accepts: "application/json",

        cache: false,
        dataType: 'json',
        error: function (jqXHR) {
            console.log("ajax error " + jqXHR.status);
        }
    };
    return $.ajax(request);
};


self.sessionStyle = {};
self.selectedColor = [];


self.drawDataOnMap = function (data) {
    console.log("requested data");
    if (self.vehicleSource.getFeatures().length > 0) {
        console.log("cleared vehicleSource");
        self.vehicleSource.clear();
    }

    for (var i = 0; i < data.length; i++) {
        self.draw(data[i])
    }

    console.log("feature updated");
}

self.draw = function (data) {
    if (self.vehicleSource.getFeatureById(data.id) != undefined) {
        self.vehicleSource.removeFeature(self.vehicleSource.getFeatureById(data.id))
    }
    var field = data;
    console.log("createing feature for data: " + field.id+":"+field.value);
    var feature = new ol.Feature(field);
    feature.setId(field.id);

    var latitude = field.latitude;
    var longitude = field.longitude;
    var point = new ol.geom.Point(ol.proj.fromLonLat([longitude, latitude]));

    var colorStyle = '#' + rainbow.colourAt(data.value);

    // if (self.sessionStyle.hasOwnProperty(field.route_id)) {
    //   colorStyle = sessionStyle[field.route_id]
    // } else {
    //   colorStyle = self.pickColor()
    //   while ($.inArray(colorStyle, self.selectedColor) > -1) {
    //      colorStyle = self.pickColor();
    //   }
    //   self.sessionStyle[field.route_id] = colorStyle;
    // }

    style = new ol.style.Style({
        image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
            color: colorStyle,
            src: 'data/dot.png',
            rotation: field.heading
        }))
    });

    feature.setGeometry(point)
    feature.setStyle(style)
    console.log("feature created")
    self.vehicleSource.addFeature(feature);

}


self.map.on('moveend', function (event) {
    console.log("map move end");

    if (self.socket != null) {
        self.socket.send("close")
        self.socket.close();
    }

    var mapExtent = self.map.getView().calculateExtent(map.getSize());
    var bottomRight = ol.extent.getBottomRight(mapExtent);
    var topLeft = ol.extent.getTopLeft(mapExtent);
    var brLonLat = ol.proj.toLonLat(bottomRight);
    var tlLonLat = ol.proj.toLonLat(topLeft);

    self.socket = self.getWebsocket();

    self.socket.onmessage = function (msg) {
        console.log("websocket")
        self.draw(jQuery.parseJSON(msg.data));
    }

    self.socket.onopen = function (e) {
        self.socket.send(tlLonLat[1] + "," + tlLonLat[0] + "," + brLonLat[1] + "," + brLonLat[0])
    }

    var timeRequest = $("#amount")
    if (timeRequest > 0) {
        self.requestVehiclesOnBoundingBox(tlLonLat[1] + "," + tlLonLat[0] + "," + brLonLat[1] + "," + brLonLat[0], timeRequest)
    }


});

self.requestVehiclesOnBoundingBox = function (bboxString, timeRequest) {
    var akkaService = self.akkaServiceBasis + bboxString;
    self.ajax(akkaService + "&time=" + timeRequest).done(function (data) {
        console.log("got data")
        self.drawDataOnMap(data);
    });
}

self.createOverlay = function () {
    return new ol.Overlay({
        element: document.getElementById('myOverlay'),
        positioning: 'top-right',
        stopEvent: false,
        insertFirst: false
    });
}


self.setCoordinateAndShow = function (coordinate, pixel) {
    $(overlay.getElement()).hide();
    $('#routeInfos').empty();
    // Set position
    overlay.setPosition(coordinate);

    var features = [];
    self.map.forEachFeatureAtPixel(pixel, function (feature, layer) {
        features.push(feature);
    });

    if (features.length > 0) {
        var info = [];
        var uniqueIds = [];


        for (var i = 0, ii = features.length; i < ii; ++i) {
            var routeId = features[i].get('route_id');

            if (typeof routeId != 'undefined' && $.inArray(routeId, uniqueIds) == -1) {
                uniqueIds.push(routeId);
            }

            info.push(features[i].get('id')+":"+features[i].get('value'));
            //maybe it's a lineString
            console.log("Geom is: " + features[i].getId());
            if (features[i].getId().indexOf("Route-") == 0) {
                info.push(features[i].get('routeId'))
                console.log("found a linestring");
                var closestPoint = features[i].getGeometry().getClosestPoint(coordinate);
                console.log("now query backend for route with closest point: " + closestPoint);
                self.routesSource.clear();
                return;
            }

            if (features[i].getId().indexOf("Cluster-") == 0) {
                console.log("found a cluster");
            }
        }
        if (uniqueIds.length > 0) {
            self.queryRoutes(uniqueIds);
            self.drawRoutes(uniqueIds);
        }


        $('#coordinate').text(info.join(', ') || '(unknown)');
        $(overlay.getElement()).show();
    }
}

self.overlay = createOverlay();

console.log("created overlay: " + self.overlay);

self.map.addOverlay(self.overlay);

self.map.on('click', function (event) {
    var coordinate = event.coordinate;
    var pixel = event.pixel;
    setCoordinateAndShow(coordinate, pixel);
});

self.pickColor = function () {
    return "#000000".replace(/0/g, function () {
        return (~~(Math.random() * 16)).toString(16);
    });
};

// $(function() {
//     $( "#slider" ).slider({
//         range: "max",
//         min: 0,
//         max: 10,
//         value: 0,
//         slide: function( event, ui ) {
//             $( "#amount" ).val( ui.value );
//
//             var mapExtent = self.map.getView().calculateExtent(map.getSize());
//             var bottomRight = ol.extent.getBottomRight(mapExtent);
//             var topLeft = ol.extent.getTopLeft(mapExtent);
//             var brLonLat = ol.proj.toLonLat(bottomRight);
//             var tlLonLat = ol.proj.toLonLat(topLeft);
//
//             if (ui.value > 0) {
//                 self.requestVehiclesOnBoundingBox(tlLonLat[1]+","+tlLonLat[0]+","+brLonLat[1]+","+brLonLat[0], ui.value)
//             } else {
//                 //clean old data
//                 console.log("cleaning old data")
//                 self.vehicleSource.clear();
//             }
//         }
//     });
//     $( "#amount" ).val( $( "#slider" ).slider( "value" ) );
//     $( "#hotspot" ).button().click(function() {
//         if ($('#hotspot').is(':checked')) {
//             console.log("draw hotspots")
//             var mapExtent = self.map.getView().calculateExtent(map.getSize());
//             var bottomRight = ol.extent.getBottomRight(mapExtent);
//             var topLeft = ol.extent.getTopLeft(mapExtent);
//             var brLonLat = ol.proj.toLonLat(bottomRight);
//             var tlLonLat = ol.proj.toLonLat(topLeft);
//
//             self.ajax(self.akkaHotSpotBasis+tlLonLat[1]+","+tlLonLat[0]+","+brLonLat[1]+","+brLonLat[0]).done(function(data){
//               self.drawHotSpotsOnMap(data)
//             });
//         } else {
//           if (self.hotspotSource.getFeatures().length > 0) {
//              console.log("cleared hotspotSource");
//              self.hotspotSource.clear();
//           }
//         }
//     });
// });