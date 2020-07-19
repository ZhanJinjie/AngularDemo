export default interface TreeNode {
    key?: string;
    label?: string;
    icon?: string;
    data?: any;
    parentKey?: string,
    children?: TreeNode[];
}