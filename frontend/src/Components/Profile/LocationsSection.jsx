import React from 'react';
import { MapPin, ExternalLink } from 'lucide-react';

export default function LocationsSection({ locations }) {
  return (
    <section>
      <h2>Locations ({locations.length})</h2>
      <div>
        {locations.map((location, index) => (
          <div key={index}>
            <MapPin />
            <div>
              <p>{location.city} , {location.country}</p>             
                <ExternalLink />
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}