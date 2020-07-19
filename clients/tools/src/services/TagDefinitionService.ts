import TagDefinition from '@/models/TagDefinition';
import HttpService from './HttpService';
import { Observable } from 'rxjs';


export default class TagDefinitionService extends HttpService<TagDefinition> {
    findByCatId(catId: string): Observable<TagDefinition[]> {
        return this.getRequest(this.url("tag-categories/" + catId + "/tagdefs"));
    }
    protected constructor() {
        super();
        this.requestBase = "tagdefs";
    }

    public static instance = new TagDefinitionService();
}