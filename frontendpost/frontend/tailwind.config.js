module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: "#00A6F2",
        secondary: "#FFEAEE",
      },
      fontFamily: {
        display: ["Inter", "sans-serif"]
      },
    },
  },
  plugins: [require('@tailwindcss/forms')],
}