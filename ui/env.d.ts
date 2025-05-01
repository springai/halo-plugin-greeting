/// <reference types="vite/client" />
/// <reference types="unplugin-icons/types/vue" />
export interface PluginTab {
  id: string;                 // 选项卡 ID，不能与设置表单的 group 重复
  label: string;              // 选项卡标题
  component: Raw<Component>;  // 选项卡面板组件
  permissions?: string[];     // 选项卡权限
}
