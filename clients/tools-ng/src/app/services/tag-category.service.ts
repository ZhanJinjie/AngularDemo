import { Injectable } from '@angular/core';
import { HttpService } from './http.service';
import { TagCategory } from '../models/tag-category';
import { TagDefinition } from '../models/tag-definition'
import { Observable } from "rxjs";
import { TagDefinitionService } from './tag-definition.service'

@Injectable({
  providedIn: 'root'
})
export class TagCategoryService extends HttpService<TagCategory> {
  getDefs(id: string): Observable<TagDefinition[]> {
    return this.tagDefSvc.findByCatId(id);
  }
  constructor(private tagDefSvc: TagDefinitionService) {
    super();
    this.requestBase = "tagCategories";
  }
}
