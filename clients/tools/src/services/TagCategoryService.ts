import HttpService from './HttpService';
import TagCategory from '../models/TagCategory'
import { Observable } from 'rxjs';
import TagDefinition from '@/models/TagDefinition';
import TagDefinitionService from "./TagDefinitionService"
export default class TagCategoryService extends HttpService<TagCategory> {
    private tagDefSvc = TagDefinitionService.instance;
    getDefs(id: string): Observable<TagDefinition[]> {
        return this.tagDefSvc.findByCatId(id);
    }
    public static instance = new TagCategoryService();
    protected constructor() {
        super();
        this.requestBase = "tag-categories";
    }
}