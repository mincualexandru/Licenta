import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
import { DataTransferComponent } from './data-transfer/data-transfer.component';
import { LoginComponent } from './login/login.component';

const appRoutes: Routes = [
 { path: '', redirectTo: '/data-transfer', pathMatch: 'full' },
 { path: 'auth', loadChildren: './auth/auth.module#AuthModule' },
 { path: 'data-transfer', component: DataTransferComponent, canActivate: [AuthGuard]},
 { path: 'login', component: LoginComponent}
]

@NgModule({
    imports: [RouterModule.forRoot(appRoutes, {preloadingStrategy: PreloadAllModules})],
    exports: [RouterModule]
})

export class AppRoutingModule {

}