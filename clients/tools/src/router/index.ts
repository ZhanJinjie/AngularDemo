import Vue from 'vue'
import VueRouter, { RouteConfig } from 'vue-router'
import ExerciseManagement from '../views/ExerciseManagement.vue'
import TagManagement from '../views/TagManagement.vue'
import OutlineManagement from '../views/OutlineManagement.vue'

Vue.use(VueRouter)

const routes: Array<RouteConfig> = [
  {
    path: '/',
    name: '试题管理',
    component: ExerciseManagement
  },
  {
    path: "/outlines",
    name: "教学大纲管理",
    component: OutlineManagement
  }
  ,
  {
    path: "/tags",
    name: "标签管理",
    component: TagManagement
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
