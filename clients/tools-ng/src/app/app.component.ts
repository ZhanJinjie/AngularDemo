import { Component } from '@angular/core';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = '题库管理工具';
  mainMenu = [
    {
      label: "题库管理",
      icon: "pi pi-fw ",
      items: [
        {
          label: "试题管理",
          icon: "pi pi-fw pi-list",
          to: "/"
        },
        {
          label: "教学大纲管理",
          icon: "pi pi-fw pi-th-large",
          to: "/outlines"
        },
        {
          label: "标签管理",
          icon: "pi pi-fw pi-tags",
          routerLink: ["tag-management"]
        }
      ]
    }
  ];
}
