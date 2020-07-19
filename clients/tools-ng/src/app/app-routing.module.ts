import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TagManagementComponent } from './components/tag-management/tag-management.component';


const routes: Routes = [
  {
    path: "tag-management",
    component: TagManagementComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
