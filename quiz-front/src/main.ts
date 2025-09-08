import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import {provideHttpClient, withInterceptors, withInterceptorsFromDi} from '@angular/common/http';
import { App } from './app/app';
import { routes } from './app/app.routes';
import { AuthInterceptor } from './app/security/auth.interceptor';
import { AuthService } from './app/services/auth.service';
//
// bootstrapApplication(App, {
//   providers: [
//     provideRouter(routes),
//     provideHttpClient(withInterceptorsFromDi()),
//     AuthService,
//   ]
// }).catch(err => console.error(err));

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([AuthInterceptor]))
  ]
});
