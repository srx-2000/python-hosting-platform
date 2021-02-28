// 该工具类用于调用localstorage对象存储数据与读入数据

export default {
  saveData(name,todos) {
    window.localStorage.setItem(name, JSON.stringify(todos))
  },
  restoreData(name) {
    return JSON.parse(window.localStorage.getItem(name) || "[]")
  }
}

