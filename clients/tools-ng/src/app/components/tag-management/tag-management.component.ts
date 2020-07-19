import { Component, OnInit } from '@angular/core';
import { from } from 'rxjs';
import { TagCategory } from '../../models/tag-category';
import { TagDefinition } from '../../models/tag-definition';
@Component({
  selector: 'app-tag-management',
  templateUrl: './tag-management.component.html',
  styleUrls: ['./tag-management.component.scss']
})
export class TagManagementComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  addNewCategory() {

  }

  editCategory() {

  }

  deleteCategory() {

  }

  onTagCatSelected() {

  }

  addNewDef() {

  }

  editDef() {

  }

  deleteDef() {

  }


  tagDefs: TagDefinition[];
  canEditCategory: boolean;

  selectedDef: TagDefinition;
  selectedCategory: TagCategory;
  categories: TagCategory;

}
