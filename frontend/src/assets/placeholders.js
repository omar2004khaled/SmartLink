// Inline SVG placeholders exported as data-URL strings
export const PROFILE_PLACEHOLDER = `data:image/svg+xml;utf8,${encodeURIComponent(`
<svg xmlns='http://www.w3.org/2000/svg' width='400' height='400' viewBox='0 0 400 400'>
  <rect width='100%' height='100%' fill='#E5E7EB'/>
  <g fill='#9CA3AF' font-family='Arial, Helvetica, sans-serif' font-size='28' text-anchor='middle'>
    <text x='50%' y='62%'>Profile</text>
  </g>
</svg>
`)}`;

export const COVER_PLACEHOLDER = `data:image/svg+xml;utf8,${encodeURIComponent(`
<svg xmlns='http://www.w3.org/2000/svg' width='1200' height='300' viewBox='0 0 1200 300'>
  <rect width='100%' height='100%' fill='#F3F4F6'/>
  <g fill='#D1D5DB' font-family='Arial, Helvetica, sans-serif' font-size='36' text-anchor='middle'>
    <text x='50%' y='50%'>Cover Image</text>
  </g>
</svg>
`)}`;
