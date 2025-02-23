// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

export default createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          primary: '#00B894',
          'primary-darken-1': '#00CEC9',
          secondary: '#2C3E50',
          'secondary-darken-1': '#1E293B',
          error: '#FF5252',
          info: '#2196F3',
          success: '#4CAF50',
          warning: '#FFC107',
          background: '#F8FAFC',
          surface: '#FFFFFF',
        },
      },
      dark: {
        dark: true,
        colors: {
          primary: '#00B894',
          'primary-darken-1': '#00CEC9',
          secondary: '#94A3B8',
          'secondary-darken-1': '#64748B',
          background: '#1E293B',
          surface: '#0F172A',
        },
      },
    },
  },
  defaults: {
    VCard: {
      elevation: 2,
    },
    VBtn: {
      elevation: 2,
      rounded: true,
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
    },
  },
}) 