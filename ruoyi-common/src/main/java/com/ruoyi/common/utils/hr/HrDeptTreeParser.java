package com.ruoyi.common.utils.hr;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.*;

/**
 * HR Excel 部门树解析工具
 *
 * Excel 列约定：Dept1 ~ Dept6，Dept1 为顶层，Dept6 为最深层。
 * 某行只填到 DeptN，DeptN+1 ~ Dept6 为空，表示该人挂在 DeptN 下。
 * 同名同父的节点视为同一部门（去重）。
 *
 * 使用示例：
 *   List<DeptNode> roots = HrDeptTreeParser.parseFromExcel(inputStream, 0);
 *   // 打印树
 *   HrDeptTreeParser.print(roots, "");
 */
public class HrDeptTreeParser {

    /** 部门列列名，顺序即层级 */
    private static final String[] DEPT_COLS = {"Dept1", "Dept2", "Dept3", "Dept4", "Dept5", "Dept6"};

    // ── 节点定义 ──────────────────────────────────────────────────────────────

    public static class DeptNode {
        /** 部门名称 */
        public final String name;
        /** 层级深度，根节点为 1 */
        public final int depth;
        /** 父节点，根节点为 null */
        public final DeptNode parent;
        /** 子节点（有序，按首次出现顺序） */
        public final List<DeptNode> children = new ArrayList<>();

        DeptNode(String name, int depth, DeptNode parent) {
            this.name   = name;
            this.depth  = depth;
            this.parent = parent;
        }

        /** 从根到本节点的完整路径，例如 "集团/研发中心/研发一组" */
        public String fullPath() {
            if (parent == null) return name;
            return parent.fullPath() + "/" + name;
        }

        /** 将本节点及所有后代以广度优先顺序收集到列表（不含自身可选） */
        public List<DeptNode> flatten() {
            List<DeptNode> result = new ArrayList<>();
            Queue<DeptNode> queue = new LinkedList<>();
            queue.add(this);
            while (!queue.isEmpty()) {
                DeptNode cur = queue.poll();
                result.add(cur);
                queue.addAll(cur.children);
            }
            return result;
        }

        @Override
        public String toString() {
            return String.format("DeptNode{name='%s', depth=%d, children=%d}", name, depth, children.size());
        }
    }

    // ── 主解析入口 ────────────────────────────────────────────────────────────

    /**
     * 从 Excel InputStream 解析部门树。
     *
     * @param is        Excel 文件流（.xlsx）
     * @param sheetIndex Sheet 索引，通常传 0
     * @return 根节点列表（通常只有一个根，但支持多根）
     */
    public static List<DeptNode> parseFromExcel(InputStream is, int sheetIndex) throws Exception {
        try (Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(sheetIndex);
            // 读表头，找各 DeptN 列的列号
            int[] colIndexes = resolveColumnIndexes(sheet.getRow(0));
            // 解析每行的部门路径链
            List<List<String>> paths = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                List<String> chain = extractChain(row, colIndexes);
                if (!chain.isEmpty()) paths.add(chain);
            }
            return buildTree(paths);
        }
    }

    /**
     * 直接从已有的部门路径链列表解析树（不需要 Excel，方便单元测试或其他数据源）。
     * 每条链是从根到叶的部门名列表，例如 ["集团", "研发中心", "研发一组"]。
     */
    public static List<DeptNode> buildTree(List<List<String>> paths) {
        // 根节点集合（key = 部门名，保持插入顺序）
        Map<String, DeptNode> rootMap = new LinkedHashMap<>();

        for (List<String> chain : paths) {
            if (chain.isEmpty()) continue;
            DeptNode parent = null;
            Map<String, DeptNode> levelMap = rootMap; // 当前层的兄弟节点 map

            for (int i = 0; i < chain.size(); i++) {
                String deptName = chain.get(i);
                DeptNode node = levelMap.computeIfAbsent(deptName, k -> {
                    DeptNode n = new DeptNode(k, i + 1, parent);
                    if (parent != null) parent.children.add(n);
                    return n;
                });
                // 为下一层准备 levelMap（子节点的兄弟 map）
                // 利用 children 顺序 + 名称做去重 map
                parent    = node;
                levelMap  = toNameMap(node.children);
            }
        }

        return new ArrayList<>(rootMap.values());
    }

    // ── 辅助方法 ──────────────────────────────────────────────────────────────

    /** 从表头行找到 Dept1~Dept6 各自的列号，-1 表示未找到 */
    private static int[] resolveColumnIndexes(Row headerRow) {
        int[] indexes = new int[DEPT_COLS.length];
        Arrays.fill(indexes, -1);
        if (headerRow == null) return indexes;
        for (Cell cell : headerRow) {
            String header = cellString(cell).trim();
            for (int i = 0; i < DEPT_COLS.length; i++) {
                if (DEPT_COLS[i].equalsIgnoreCase(header)) {
                    indexes[i] = cell.getColumnIndex();
                }
            }
        }
        return indexes;
    }

    /** 从一行数据提取 Dept1~Dept6 的非空链，遇到空值截断（之后的忽略） */
    private static List<String> extractChain(Row row, int[] colIndexes) {
        List<String> chain = new ArrayList<>();
        for (int idx : colIndexes) {
            if (idx < 0) continue;
            Cell cell = row.getCell(idx);
            String val = cellString(cell).trim();
            if (val.isEmpty()) break; // 中间或末尾为空则截断
            chain.add(val);
        }
        return chain;
    }

    /** 将子节点列表转为 name→node 的有序 map（用于去重查找） */
    private static Map<String, DeptNode> toNameMap(List<DeptNode> nodes) {
        Map<String, DeptNode> map = new LinkedHashMap<>();
        for (DeptNode n : nodes) map.put(n.name, n);
        return map;
    }

    private static String cellString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default:      return "";
        }
    }

    // ── 调试打印 ──────────────────────────────────────────────────────────────

    /** 递归打印树结构，方便调试 */
    public static void print(List<DeptNode> nodes, String indent) {
        for (DeptNode node : nodes) {
            System.out.println(indent + "├─ " + node.name + " (depth=" + node.depth + ", path=" + node.fullPath() + ")");
            print(node.children, indent + "│  ");
        }
    }

    // ── 测试入口 ──────────────────────────────────────────────────────────────

    /**
     * 直接运行此 main 方法即可验证解析逻辑，无需 Excel 文件。
     * 模拟数据覆盖以下场景：
     *  - 正常6层链
     *  - 中途截断（只到第4层）
     *  - 同名同父去重
     *  - 多根节点
     */
    public static void main(String[] args) throws Exception {

        // ── 场景1：用路径链直接构建（不依赖 Excel） ─────────────────────────
        System.out.println("════════ 场景1：路径链解析 ════════");
        List<List<String>> paths = Arrays.asList(
            // 正常6层
            Arrays.asList("总公司", "研发中心", "后端部", "Java组"),
            Arrays.asList("总公司", "研发中心", "后端部", "Go组"),
            Arrays.asList("总公司", "研发中心", "前端部"),
            // 只到第3层
            Arrays.asList("总公司", "研发中心", "测试部"),
            // 同名同父（应去重，不重复创建"行政部"）
            Arrays.asList("总公司", "行政部", "人事组"),
            Arrays.asList("总公司", "行政部", "财务组"),
            // 第二个根节点
            Arrays.asList("分公司", "华南区", "广州办事处"),
            Arrays.asList("分公司", "华南区", "深圳办事处"),
            Arrays.asList("分公司", "华北区")
        );

        List<DeptNode> roots = buildTree(paths);
        print(roots, "");

        System.out.println("\n──── 展平（BFS）第一棵树下所有节点 ────");
        roots.get(0).flatten().forEach(n ->
            System.out.printf("  depth=%-2d  %-20s  %s%n", n.depth, n.name, n.fullPath())
        );

        // ── 场景2：模拟 Excel 行数据（不实际读文件） ──────────────────────
        System.out.println("\n════════ 场景2：模拟 Excel 行解析 ════════");
        // 每行对应 [Dept1, Dept2, Dept3, Dept4, Dept5, Dept6]，空串表示该列为空
        String[][] excelRows = {
            {"总公司", "研发中心", "后端部", "Java组", "",       ""},   // 只到第4层
            {"总公司", "研发中心", "后端部", "Java组", "基础架构", ""},  // 到第5层
            {"总公司", "研发中心", "后端部", "Go组",   "",       ""},
            {"总公司", "研发中心", "前端部", "",       "",       ""},   // 只到第3层
            {"总公司", "行政部",   "人事组", "",       "",       ""},
            {"总公司", "行政部",   "财务组", "",       "",       ""},
            {"总公司", "行政部",   "财务组", "",       "",       ""},   // 重复行，应去重
        };

        List<List<String>> excelPaths = new ArrayList<>();
        for (String[] row : excelRows) {
            List<String> chain = new ArrayList<>();
            for (String val : row) {
                if (val == null || val.trim().isEmpty()) break;
                chain.add(val.trim());
            }
            if (!chain.isEmpty()) excelPaths.add(chain);
        }

        List<DeptNode> roots2 = buildTree(excelPaths);
        print(roots2, "");

        System.out.println("\n──── 验证去重：行政部子节点数（应为2，不是3） ────");
        roots2.get(0).flatten().stream()
            .filter(n -> "行政部".equals(n.name))
            .findFirst()
            .ifPresent(n -> System.out.println("  行政部 children=" + n.children.size() + " → " + n.children.stream().map(c -> c.name).toList()));

        // ── 场景3：读取真实 Excel 文件（取消注释并填写路径后运行） ─────────
        System.out.println("\n════════ 场景3：读取真实 Excel（默认跳过） ════════");
        String excelPath = ""; // ← 填写你的 Excel 绝对路径，如 "D:/staff.xlsx"
        if (!excelPath.isEmpty()) {
            try (java.io.FileInputStream fis = new java.io.FileInputStream(excelPath)) {
                List<DeptNode> roots3 = parseFromExcel(fis, 0);
                System.out.println("解析到根节点数：" + roots3.size());
                print(roots3, "");
            }
        } else {
            System.out.println("  （跳过，excelPath 未配置）");
        }
    }
}
