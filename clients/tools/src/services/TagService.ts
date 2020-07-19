// import HttpService from './HttpService';
// import TagCategory from '@/models/TagCategory';
// import Tag from '@/models/Tag';
// import TagDefinition from '@/models/TagDefinition';

// export default class TagService extends HttpService<Tag> {
//     protected constructor() {
//         super();
//         this.requestBase = "tags"
//     }
//     public static instance = new TagService();
//     // getDefs(): Promise<TagDefinition[]> {
//     //     return this.getRequest(this.url("tagdefs"));
//     // }
//     // deleteDef(defId: string): Promise<void> {
//     //     return this.deleteRequest(this.url("tagdefs/" + defId))
//     // }
//     // updateDef(def: TagDefinition): Promise<void> {
//     //     return this.putRequest(this.url("tagdefs/" + def.id), def)
//     // }
//     // createDef(def: TagDefinition): Promise<string> {
//     //     return this.postRequest(this.url("tagdefs"), def);
//     // }

//     // public static instance = new TagService();

//     // getCategories(): Promise<TagCategory[]> {
//     //     return this.getRequest(this.url("tagCategories"));
//     // }
//     // getDefByCategoryId(catId: string): Promise<TagDefinition[]> {
//     //     return this.getRequest(this.url("tagCategories/" + catId + "/tagdefs"));
//     // }
//     // createCategory(cat: TagCategory): Promise<string> {
//     //     return this.postRequest(this.url("tagCategories"), cat);
//     // }
//     // updateCategory(cat: TagCategory) {
//     //     return this.putRequest(this.url("tagCategories/" + cat.id), cat);
//     // }
//     // deleteCategory(catId: string) {
//     //     return this.deleteRequest(this.url("tagCategories/" + catId));
//     // }
// }