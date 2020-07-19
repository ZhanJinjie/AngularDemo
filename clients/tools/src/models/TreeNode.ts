import Tag from './Tag';

export default interface TreeNode {
    id?: string;
    name?: string;
    parentId?: string;
    description?: string;
    children?: TreeNode[];
    tags?: Tag[];
}